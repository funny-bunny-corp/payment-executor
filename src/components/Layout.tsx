import React from 'react';
import { 
  AppBar, 
  Toolbar, 
  Typography, 
  Drawer, 
  List, 
  ListItem, 
  ListItemIcon, 
  ListItemText, 
  Box,
  CssBaseline,
  IconButton,
  useTheme,
  useMediaQuery
} from '@mui/material';
import {
  Dashboard,
  Payment,
  History,
  Refresh,
  Menu as MenuIcon,
  CreditCard,
  Analytics
} from '@mui/icons-material';
import { Link, useLocation } from 'react-router-dom';
import styled from 'styled-components';

const drawerWidth = 240;

const StyledDrawer = styled(Drawer)`
  width: ${drawerWidth}px;
  flex-shrink: 0;
  
  .MuiDrawer-paper {
    width: ${drawerWidth}px;
    box-sizing: border-box;
    background: linear-gradient(135deg, #87CEEB, #DDA0DD);
    color: white;
  }
`;

const StyledListItem = styled(ListItem)<{ active?: boolean }>`
  margin: 8px 16px;
  border-radius: 8px;
  background: ${props => props.active ? 'rgba(255, 255, 255, 0.2)' : 'transparent'};
  
  &:hover {
    background: rgba(255, 255, 255, 0.1);
  }
  
  .MuiListItemIcon-root {
    color: white;
  }
  
  .MuiListItemText-root {
    color: white;
  }
`;

const MainContent = styled(Box)`
  flex-grow: 1;
  padding: 24px;
  background: #F8F9FA;
  min-height: 100vh;
`;

const StyledAppBar = styled(AppBar)`
  background: linear-gradient(135deg, #87CEEB, #DDA0DD) !important;
  z-index: 1201 !important;
`;

interface LayoutProps {
  children: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const [mobileOpen, setMobileOpen] = React.useState(false);
  const location = useLocation();

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  const menuItems = [
    { text: 'Dashboard', icon: <Dashboard />, path: '/' },
    { text: 'New Payment', icon: <Payment />, path: '/payment' },
    { text: 'Transactions', icon: <History />, path: '/transactions' },
    { text: 'Refunds', icon: <Refresh />, path: '/refunds' },
    { text: 'Analytics', icon: <Analytics />, path: '/analytics' }
  ];

  const drawer = (
    <div>
      <Toolbar>
        <Box display="flex" alignItems="center" gap={2}>
          <CreditCard />
          <Typography variant="h6" component="div">
            Paymentic
          </Typography>
        </Box>
      </Toolbar>
      <List>
        {menuItems.map((item) => (
          <StyledListItem
            key={item.text}
            component={Link}
            to={item.path}
            active={location.pathname === item.path}
            onClick={isMobile ? handleDrawerToggle : undefined}
          >
            <ListItemIcon>{item.icon}</ListItemIcon>
            <ListItemText primary={item.text} />
          </StyledListItem>
        ))}
      </List>
    </div>
  );

  return (
    <Box sx={{ display: 'flex' }}>
      <CssBaseline />
      <StyledAppBar
        position="fixed"
        sx={{
          width: isMobile ? '100%' : `calc(100% - ${drawerWidth}px)`,
          ml: isMobile ? 0 : `${drawerWidth}px`,
        }}
      >
        <Toolbar>
          {isMobile && (
            <IconButton
              color="inherit"
              aria-label="open drawer"
              edge="start"
              onClick={handleDrawerToggle}
              sx={{ mr: 2 }}
            >
              <MenuIcon />
            </IconButton>
          )}
          <Typography variant="h6" component="div">
            Payment Processing System
          </Typography>
        </Toolbar>
      </StyledAppBar>
      <Box
        component="nav"
        sx={{ width: { md: drawerWidth }, flexShrink: { md: 0 } }}
      >
        {isMobile ? (
          <Drawer
            variant="temporary"
            open={mobileOpen}
            onClose={handleDrawerToggle}
            ModalProps={{
              keepMounted: true, // Better open performance on mobile.
            }}
            sx={{
              display: { xs: 'block', md: 'none' },
              '& .MuiDrawer-paper': { 
                boxSizing: 'border-box', 
                width: drawerWidth,
                background: 'linear-gradient(135deg, #87CEEB, #DDA0DD)'
              },
            }}
          >
            {drawer}
          </Drawer>
        ) : (
          <StyledDrawer variant="permanent" open>
            {drawer}
          </StyledDrawer>
        )}
      </Box>
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: 3,
          width: { md: `calc(100% - ${drawerWidth}px)` },
        }}
      >
        <Toolbar />
        <MainContent>
          {children}
        </MainContent>
      </Box>
    </Box>
  );
};

export default Layout;